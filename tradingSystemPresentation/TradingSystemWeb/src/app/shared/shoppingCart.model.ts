import {Product} from './product.model';
import {ShoppingBag} from './shoppingBag.model';

export class ShoppingCart {
  constructor(public shoppingBags: Map<number, ShoppingBag>) {}
}
