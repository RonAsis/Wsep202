import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Policy} from '../../../../../shared/policy.model';
import {StoreService} from '../../../../../services/store.service';
import {Product} from '../../../../../shared/product.model';
import {IDropdownSettings, ListItem} from 'ng-multiselect-dropdown/multiselect.model';
import {AmountProductsComponent} from '../../discount/add-discount/amount-products/amount-products.component';
import {MatDialog} from '@angular/material/dialog';
import {Store} from '../../../../../shared/store.model';
import {Discount} from '../../../../../shared/discount.model';
import {WeekDay} from '@angular/common';
import {Countries} from '../../../../../shared/countries.model';

@Component({
  selector: 'app-add-policy',
  templateUrl: './add-policy.component.html',
  styleUrls: ['./add-policy.component.css']
})
export class AddPolicyComponent implements OnInit {

  constructor(private storeService: StoreService) { }

  @Input() policy: Policy;
  @Input() store: Store;

  @Output() policyItemAdded = new EventEmitter<Discount>();

  @ViewChild('min', {static: false}) min: ElementRef;
  @ViewChild('max', {static: false}) max: ElementRef;
  @ViewChild('description', {static: false}) description: ElementRef;

  optionsProductUnderPolicy: Product[];
  selectedProductUnderPolicy: number;
  productUnderPolicySettings: IDropdownSettings;

  optionsCountriesPermitted: string[];
  selectedCountriesPermitted: string[];
  countriesPermittedSettings: IDropdownSettings;

  optionsStoreWorkDays: string[];
  selectedStoreWorkDays: string[];
  storeWorkDaysSettings: IDropdownSettings;

  optionsComposedPolicies: Policy[];
  selectedComposedPolicies: Policy[];
  composedPoliciesSettings: IDropdownSettings;

  policyType: string;

  compositeOperators: string[];
  selectedComposite: string;
  messageColor: string;
  message: string;
  countries: Countries;

  ngOnInit(): void {
    this.initComp();
    this.storeService.policySelected.subscribe(response => {
      this.policy = response;
      this.initComp();
    });
  }

  private initComp(): void {
    this.policyType = 'all store';
    this.selectedCountriesPermitted = [];
    this.selectedStoreWorkDays = [];
    this.selectedComposedPolicies = [];
    this.countries = new Countries();

    this.initSettings();

    this.optionsProductUnderPolicy = this.store.products.slice();
    this.optionsStoreWorkDays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    this.optionsCountriesPermitted = this.countries.getCountries();

    this.storeService.getAllPurchasePolicies(this.store.storeId)
      .subscribe(response => {
        if (response !== null && response !== undefined) {
          this.optionsComposedPolicies = response;
        }
      });

    this.storeService.getCompositeOperators(this.store.storeId)
      .subscribe(response => {
        if (response !== null && response !== undefined) {
          this.compositeOperators = response;
        }
      });

    if (this.policy !== null && this.policy !== undefined) {
      this.selectedProductUnderPolicy =
        this.policy.productSn !== undefined && this.policy.productSn !== null ?
          this.policy.productSn : -1;
      this.selectedCountriesPermitted =
        this.policy.countriesPermitted !== undefined && this.policy.countriesPermitted !== null ?
          this.policy.countriesPermitted : [];
      this.selectedStoreWorkDays =
        this.policy.storeWorkDays !== undefined && this.policy.storeWorkDays !== null ?
          this.policy.storeWorkDays.map(value => value.toString()) : [];
      this.selectedComposedPolicies =
        this.policy.composedPurchasePolicies !== undefined && this.policy.composedPurchasePolicies !== null ?
          this.policy.composedPurchasePolicies : [];
      this.selectedComposite = this.policy.compositeOperator;
      this.policyType = this.policy.policyType;
    }
  }

  private initSettings(){
    this.productUnderPolicySettings = {
      singleSelection: true,
      idField: 'productSn',
      textField: 'name',
      allowSearchFilter: true
    };

    this.storeWorkDaysSettings = {
      singleSelection: false,
      textField: 'name',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      allowSearchFilter: true
    };

    this.countriesPermittedSettings = {
      singleSelection: false,
      textField: 'name',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      allowSearchFilter: true
    };

    this.composedPoliciesSettings = {
      singleSelection: false,
      idField: 'purchaseId',
      textField: 'description',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      allowSearchFilter: true
    };
  }

  errorMessage(message: string) {
    this.message = message;
    this.messageColor = 'red';
  }

  sucMessage() {
    this.message = 'the policy is added';
    this.messageColor = 'blue';
  }

  onAllStore() {
    this.errorMessage('');
    if (this.description !== null && this.description !== undefined) {
      this.description.nativeElement.value = '';
    }
    this.policyType = 'all store';
  }

  onSpecificProduct() {
    this.errorMessage('');
    if (this.description !== null && this.description !== undefined) {
      this.description.nativeElement.value = '';
    }
    this.policyType = 'specific product';
  }

  onSystem() {
    this.errorMessage('');
    if (this.description !== null && this.description !== undefined) {
      this.description.nativeElement.value = '';
    }
    this.policyType = 'on system';
  }

  onUser() {
    this.errorMessage('');
    if (this.description !== null && this.description !== undefined) {
      this.description.nativeElement.value = '';
    }
    this.policyType = 'on user';
  }

  onComposePolicy() {
    this.errorMessage('');
    if (this.description !== null && this.description !== undefined) {
      this.description.nativeElement.value = '';
    }
    this.policyType = 'compose';
  }

  onSelectProductUnderPolicy(productItem: any) {
    this.selectedProductUnderPolicy = this.store.products.find(product => product.productSn === productItem.productSn).productSn;
  }


  onSelectCountriesPermitted(country: ListItem) {
    this.selectedCountriesPermitted.push(this.optionsCountriesPermitted.find(value => value === country.text));
  }

  onSelectStoreWorkDays(day: ListItem) {
    this.selectedStoreWorkDays.push(this.optionsStoreWorkDays.find(value => value === day.text));
  }

  onSelectedCompositeOperator(composite: string) {
    this.selectedComposite = composite;
  }

  private convertDayToInt(value: string): number {
    switch (value) {
      case 'Sunday':
        return 1;
      case 'Monday':
        return 2;
      case 'Tuesday':
        return 3;
      case 'Wednesday':
        return 4;
      case 'Thursday':
        return 5;
      case 'Friday':
        return 6;
      case 'Saturday':
        return 7;
    }
  }

  onAddDiscount() {
    if (this.min !== null && this.min !== undefined && (this.min.nativeElement.value === null
      || this.min.nativeElement.value === undefined)) {
      this.errorMessage('You must type minimum value');
    } else if (this.max !== null && this.max !== undefined && (this.max.nativeElement.value === null
      || this.max.nativeElement.value === undefined)) {
      this.errorMessage('You must type maximum value');
    } else if (this.description.nativeElement.value === null || this.description.nativeElement.value === undefined ||
      this.description.nativeElement.value.length === 0) {
      this.errorMessage('You must type description');
    } else if ((this.selectedProductUnderPolicy === null || this.selectedProductUnderPolicy === undefined) && this.policyType === 'specific product') {
      this.errorMessage('You must choose a product');
    } else if ((this.selectedCountriesPermitted === null || this.selectedCountriesPermitted === undefined ||
      this.selectedCountriesPermitted.length === 0) && this.policyType === 'on user') {
      this.errorMessage('You must choose at least one country');
    } else if ((this.selectedStoreWorkDays === null || this.selectedStoreWorkDays === undefined ||
      this.selectedStoreWorkDays.length === 0) && this.policyType === 'on system') {
      this.errorMessage('You must choose at least one day');
    } else {
      this.errorMessage('');
      const policy = new Policy(
        this.policy !== null && this.policy !== undefined ? this.policy.purchaseId : -1,
        this.policy.purchaseType,
        this.policyType,
        this.description !== null && this.description !== undefined ? this.description.nativeElement.value : '',
        this.selectedCountriesPermitted,
        this.selectedStoreWorkDays.map(value => this.convertDayToInt(value)),
        this.min !== null && this.min !== undefined ? this.min.nativeElement.value : 1,
        this.max !== null && this.max !== undefined ? this.max.nativeElement.value : 1,
        this.selectedProductUnderPolicy,
        this.selectedComposite,
        this.optionsComposedPolicies.filter(p => this.selectedComposedPolicies
          .filter(p1 => p1.purchaseId === p.purchaseId).length === 1));
      this.storeService.addPolicy(this.store.storeId, policy)
        .subscribe(response => {
          if (response !== undefined && response !== null) {
            this.policyItemAdded.emit(response);
          }
        });
      this.sucMessage();
    }
  }
}
