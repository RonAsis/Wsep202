import {Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Product} from '../../shared/product.model';
import {ProductService} from '../../services/product.service';
import {ProductListComponent} from './product-list/product-list.component';
import {Store} from '../../shared/store.model';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  selectedProduct: Product;
  @Input() store: Store;

  constructor(private productService: ProductService) { }

  ngOnInit(): void {
    this.productService.productSelectedEvent
      .subscribe(
        (product: Product) => {
          this.selectedProduct = product;
        }
      );
  }

}
