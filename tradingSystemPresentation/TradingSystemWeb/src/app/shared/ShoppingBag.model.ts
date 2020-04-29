import {Product} from './product.model';

export class ShoppingBag {
  constructor(public productListFromStore: Map<Product, number>) {}
}
