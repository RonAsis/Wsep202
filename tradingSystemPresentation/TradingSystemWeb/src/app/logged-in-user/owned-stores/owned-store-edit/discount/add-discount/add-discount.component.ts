import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Store} from '../../../../../shared/store.model';
import {StoreService} from '../../../../../services/store.service';
import {IDropdownSettings, ListItem} from 'ng-multiselect-dropdown/multiselect.model';
import {Discount} from '../../../../../shared/discount.model';
import {Product} from '../../../../../shared/product.model';

@Component({
  selector: 'app-add-discount',
  templateUrl: './add-discount.component.html',
  styleUrls: ['./add-discount.component.css']
})
export class AddDiscountComponent implements OnInit {

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

  constructor(private storeService: StoreService) {
    this.selectedProductUnderDiscount = [];
    this.selectedProductsForApplyDiscounts = [];
    this.selectedComposedDiscounts = [];

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
  }

  private init() {
    this.optionsProductUnderDiscount = this.store.products;
    console.log(this.optionsProductUnderDiscount);
    this.storeService.getDiscounts(this.store.storeId)
      .subscribe(response => {
        if (response !== null && response !== undefined) {
          this.optionsComposedDiscounts = response;
        }
      });
    this.optionsProductsForApplyDiscounts = this.store.products
      .filter(product => !this.selectedProductUnderDiscount.includes(product));
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
    const discount = new Discount(
      0,
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
          console.log('added');
        }
      });
  }

  onSelectProductUnderDiscount(productItem: any) {
    console.log(productItem);
    console.log(this.optionsProductsForApplyDiscounts);
    this.optionsProductsForApplyDiscounts = this.optionsProductsForApplyDiscounts
      .filter(product => product.productSn !== productItem.productSn);
    console.log(this.optionsProductsForApplyDiscounts
      .filter(product => product.productSn !== productItem.id));
    console.log(this.optionsProductsForApplyDiscounts);
    this.selectedProductsForApplyDiscountsDisabled = false;
  }

  onSelectProductsForApplyDiscount(productItem: ListItem) {
    this.selectedProductUnderDiscountDisabled = true;
  }

  onDeSelectProductsForApplyDiscount(productItem: ListItem) {
    if (this.selectedProductsForApplyDiscounts.length === 0){
      this.selectedProductUnderDiscountDisabled = false;
    }
  }
}
