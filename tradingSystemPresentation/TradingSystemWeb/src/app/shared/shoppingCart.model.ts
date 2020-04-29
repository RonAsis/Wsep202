import {Product} from './product.model';
import {ShoppingBag} from './ShoppingBag.model';

export class ShoppingCart {
  constructor(public shoppingBags: Map<number, ShoppingBag>) {}
}
