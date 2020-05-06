import {EventEmitter, Injectable, Output} from '@angular/core';
import {Product} from '../shared/product.model';
import {HttpService} from './http.service';
import {Store} from '../shared/store.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  productSelectedEvent = new EventEmitter<Product>();
  filterByPriceEvent = new EventEmitter<{min: number, max: number}>();

  constructor(private httpService: HttpService) {
  }

  getProducts() {
    return this.httpService.getProducts();
  }

  getProductsStore(store: Store) {
    return store.products;
  }

  filterByPrice(range: {min: number, max: number} ) {
    return [];
  }

  getCategories() {
    return this.httpService.getCategories();
  }
}
