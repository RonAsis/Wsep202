import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Product} from '../../../../../shared/product.model';
import {UserService} from '../../../../../services/user.service';
import {StoreService} from '../../../../../services/store.service';
import {ProductService} from '../../../../../services/product.service';

@Component({
  selector: 'app-product-edit-item',
  templateUrl: './product-edit-item.component.html',
  styleUrls: ['./product-edit-item.component.css']
})
export class ProductEditItemComponent implements OnInit {

  categories: string[];
  @ViewChild('nameProductItem', {static: false}) nameProductItem: ElementRef;
  @ViewChild('amountProductItem', {static: false}) amountProductItem: ElementRef;
  @ViewChild('costProductItem', {static: false}) costProductItem: ElementRef;

  @Input()productItem: Product;
  @Output() productItemDeleted = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();
  @Output() productItemChanged = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();

  selectedCategory: string;

  constructor(private storeService: StoreService, private productService: ProductService) {

  }

  ngOnInit() {
    this.productService.getCategories().subscribe(categories => {
        if (categories !== null && categories !== undefined) {
          this.categories = categories ;
        }
      }
    );
    console.log(this.productItem);
    this.selectedCategory = this.productItem.category;
  }

  onProductItemDeleted(event) {
    this.productItemDeleted.emit({
      productSn: this.productItem.productSn,
      storeId: this.productItem.storeId
    });
  }

  onProductItemChanged(event) {
    this.storeService.editProduct(this.productItem.storeId, this.productItem.productSn,
      this.nameProductItem.nativeElement.value, this.selectedCategory, this.amountProductItem.nativeElement.value,
      this.costProductItem.nativeElement.value
      ).subscribe(response => {
        if (response){
          this.productItemChanged.emit({productSn: this.productItem.productSn, storeId: this.productItem.storeId});
        }
    });
  }

  onSelectCategory(category: string) {
    this.selectedCategory = category;
    this.onProductItemChanged(null);
  }
}
