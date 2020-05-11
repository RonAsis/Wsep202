import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {ProductService} from '../../../../../services/product.service';
import {StoreService} from '../../../../../services/store.service';
import {Store} from '../../../../../shared/store.model';
import {Product} from '../../../../../shared/product.model';

@Component({
  selector: 'app-add-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.css']
})
export class AddProductComponent implements OnInit {
  @Input() store: Store;
  selectedCategory: string;
  categories: string[];
  message: string;
  messageColor: string;
  @ViewChild('productNameInput', {static: false}) productNameInput: ElementRef;
  @ViewChild('amountProducts', {static: false}) amountProducts: ElementRef;
  @ViewChild('cost', {static: false}) cost: ElementRef;
  @Output() productItemAdded = new EventEmitter<Product>();
  constructor(private productService: ProductService, private storeService: StoreService) { }

  ngOnInit(): void {
    this.productService.getCategories().subscribe(categories => {
        if (categories !== null && categories !== undefined) {
          this.categories = categories ;
          this.selectedCategory = this.categories[0];
        }
      }
    );
  }

  onAddProduct() {
    if (this.productNameInput.nativeElement.value === undefined ||
      this.productNameInput.nativeElement.value.length < 1){
      this.errorMessage('You must type product name');
    }else{
      this.storeService.addProduct(this.store.storeId, this.productNameInput.nativeElement.value,
        this.selectedCategory, this.amountProducts.nativeElement.value,
        this.cost.nativeElement.value).subscribe(response => {
          if (response){
            this.sucMessage();
            this.clearFields();
          }
      });
    }
  }

  errorMessage(message: string){
    this.message = message;
    this.messageColor = 'red';
  }

  sucMessage(){
    this.message = 'the product is added';
    this.messageColor = 'blue';
  }

  onSelectCategory(category: string) {
    this.selectedCategory = category;
  }

  private clearFields() {
    this.productNameInput.nativeElement.value = '';
    this.cost.nativeElement.value = 1;
    this.amountProducts.nativeElement.value = 1;
    this.selectedCategory = this.categories[0];
  }
}
