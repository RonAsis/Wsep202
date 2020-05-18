import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Store} from '../../../../../shared/store.model';
import {StoreService} from '../../../../../services/store.service';
import {IDropdownSettings, ListItem} from 'ng-multiselect-dropdown/multiselect.model';
import {Discount} from '../../../../../shared/discount.model';
import {Product} from '../../../../../shared/product.model';
import {MatDialog} from '@angular/material/dialog';
import {AmountProductsComponent} from './amount-products/amount-products.component';

@Component({
  selector: 'app-add-discount',
  templateUrl: './add-discount.component.html',
  styleUrls: ['./add-discount.component.css']
})
export class AddDiscountComponent implements OnInit {

  @Input()discount: Discount;
  optionsProductUnderDiscount: Product[];
  selectedProductUnderDiscount: Product[];
  productUnderDiscountSettings: IDropdownSettings;

  optionsProductsForApplyDiscounts: Product[];
  selectedProductsForApplyDiscounts: Product[];
  productsForApplyDiscountSettings: IDropdownSettings;

  optionsComposedDiscounts: Discount[];
  selectedComposedDiscounts: Discount[];
  composedDiscountsSettings: IDropdownSettings;


  @ViewChild('discountPercentage', {static: false}) discountPercentage: ElementRef;
  @ViewChild('endTime', {static: false}) endTime: ElementRef;
  @ViewChild('description', {static: false}) description: ElementRef;
  @ViewChild('amountOfProductsForApplyDiscounts', {static: false}) amountOfProductsForApplyDiscounts: ElementRef;
  @ViewChild('minPrice', {static: false}) minPrice: ElementRef;

  @Output() discountItemAdded = new EventEmitter<Discount>();
  @Input() store: Store;

  messageColor: string;
  message: string;
  compositeOperators: string[];
  selectedComposite: string;
  selectedProductsForApplyDiscountsDisabled = true;
  selectedProductUnderDiscountDisabled = false;
  today: string;

  constructor(private storeService: StoreService, public dialog: MatDialog) {
    this.selectedProductUnderDiscount = [];
    this.selectedProductsForApplyDiscounts = [];
    this.selectedComposedDiscounts = [];
    this.today = new Date().toISOString().split('T')[0];

    this.productUnderDiscountSettings = {
      singleSelection: false,
      idField: 'productSn',
      textField: 'name',
      selectAllText: 'Select All Store',
      unSelectAllText: 'UnSelect All Store',
      allowSearchFilter: true
    };

    this.productsForApplyDiscountSettings = {
      singleSelection: false,
      idField: 'productSn',
      textField: 'name',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      allowSearchFilter: true
    };

    this.composedDiscountsSettings = {
      singleSelection: false,
      idField: 'discountId',
      textField: 'description',
      selectAllText: 'Select All',
      unSelectAllText: 'UnSelect All',
      allowSearchFilter: true
    };
  }

  ngOnInit(): void {
    this.init();
    this.storeService.getCompositeOperators(this.store.storeId)
      .subscribe(response => {
        if (response !== null && response !== undefined) {
          this.compositeOperators = response;
        }
      });
    if (this.discount !== null && this.discount !== undefined){
      this.selectedProductUnderDiscount = this.discount.productsUnderThisDiscount;
      this.selectedProductsForApplyDiscounts = this.discount.amountOfProductsForApplyDiscounts;
      this.selectedComposedDiscounts = this.discount.composedDiscounts;
    }
  }

  private init() {
    this.optionsProductsForApplyDiscounts = this.store.products.slice();
    console.log(this.optionsProductsForApplyDiscounts);
    this.storeService.getDiscounts(this.store.storeId)
      .subscribe(response => {
        if (response !== null && response !== undefined) {
          this.optionsComposedDiscounts = response;
        }
      });
    this.optionsProductUnderDiscount = this.store.products.slice();
  }

  errorMessage(message: string) {
    this.message = message;
    this.messageColor = 'red';
  }

  sucMessage() {
    this.message = 'the manager is added';
    this.messageColor = 'blue';
  }


  onSelectedCompositeOperator(composite: string) {
    this.selectedComposite = composite;
  }

  onAddDiscount() {
    if (this.discountPercentage.nativeElement.value === null || this.discountPercentage.nativeElement.value === undefined ){
      this.errorMessage('You must type discountPercentage');
    }else if (this.selectedProductsForApplyDiscounts.length === 0) {
      this.errorMessage('You must select products for apply discount');
    }else if (this.description.nativeElement.value === null || this.description.nativeElement.value === undefined ||
      this.description.nativeElement.value.length === 0 ) {
      this.errorMessage('You must type description');
    }else if (this.endTime.nativeElement.value === null || this.endTime.nativeElement.value === undefined  ) {
      this.errorMessage('You must select end time');
    }else {
      const discount = new Discount(
        this.discount !== null && this.discount !== undefined ? this.discount.discountId : -1,
        this.discountPercentage.nativeElement.value,
        this.endTime.nativeElement.value,
        this.selectedProductUnderDiscount,
        this.description.nativeElement.value,
        this.selectedProductsForApplyDiscounts,
        this.minPrice.nativeElement.value,
        this.selectedComposedDiscounts,
        this.selectedComposite);
      this.storeService.addDiscount(this.store.storeId, discount)
        .subscribe(response => {
          if (response !== undefined && response !== null) {
            this.optionsComposedDiscounts.push(response);
          }
        });
    }
  }

  onSelectProductUnderDiscount(productItem: any) {
    const dialogRef = this.dialog.open(AmountProductsComponent, {
      width: '250px',
    });
    dialogRef.afterClosed().subscribe((result: {isNotWith: boolean, amount: number}) => {
        if (!result.isNotWith){
          const productFound: Product = this.selectedProductUnderDiscount.find(product => product.productSn === productItem.productSn);
          productFound.amount = result.amount;
          console.log( productFound.amount);
          console.log(this.selectedProductUnderDiscount);
        }else {
          const productFound: Product = this.selectedProductUnderDiscount.find(product => product.productSn === productItem.productSn);
          productFound.amount = -1;
          console.log( productFound.amount);
          console.log(this.selectedProductUnderDiscount);
        }
    });
  }

  onSelectProductsForApplyDiscount(productItem: any) {
    const dialogRef = this.dialog.open(AmountProductsComponent, {
      width: '250px',
    });
    dialogRef.afterClosed().subscribe((result: {with: string, amount: number}) => {
      if (result.with === 'with'){
        const productFound: Product = this.selectedProductsForApplyDiscounts.find(product => product.productSn === productItem.productSn);
        productFound.amount = result.amount;
      }else {
        const productFound: Product = this.selectedProductsForApplyDiscounts.find(product => product.productSn === productItem.productSn);
        productFound.amount = -1;
      }
    });
  }

}
