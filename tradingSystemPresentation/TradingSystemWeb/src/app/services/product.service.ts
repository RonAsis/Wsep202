import {EventEmitter, Injectable, Output} from '@angular/core';
import {Product} from '../shared/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  productSelectedEvent = new EventEmitter<Product>();
  filterByPriceEvent = new EventEmitter<{min: number, max: number}>();
  private guestUrl: string;
  @Output() products: Product[];

  constructor() {
    this.guestUrl = 'http://localhost:8080/guest';
    this.products = [
      new Product(1, 's', 's', 1, 1, 1, 1 , 1, 's'),
      new Product(343434, 's', 's', 1233243232, 1, 1, 1 , 1, 's'),
      new Product(343434, 's', 's', 1233243232, 1, 9, 1 , 1, 's')

    ];
  }

  getProducts() {
    return this.products;
  }

  getProductsStore(storeId: number) {
    return [];
  }

  filterByPrice(range: {min: number, max: number} ) {
    return [];
  }
}
