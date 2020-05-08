import {Product} from './product.model';
import {ShoppingBag} from './shoppingBag.model';

export class ShoppingCart {
  constructor(public shoppingBags: Map<number, ShoppingBag>) {}

  public addProductAmounts(productSn: number, storeId: number, amount: number){
    this.shoppingBags.get(storeId).addProductAmounts(productSn, amount);
  }

  removeCartItem(productSn: number, storeId: number) {
    const shoppingBag = this.shoppingBags.get(storeId);
    shoppingBag.removeCartItem(productSn);
    if (shoppingBag.productListFromStore.size === 0){
      this.shoppingBags.delete(storeId);
    }
  }

  addToShoppingCart(product: Product, amountProducts: number) {
    const storeId = product.storeId;
    let shoppingBag = this.shoppingBags.get(storeId);
    if (shoppingBag === undefined) {
      const products = new Map<Product, number>();
      products.set(product, amountProducts);
      shoppingBag = new ShoppingBag(products);
      this.shoppingBags.set(storeId, shoppingBag);
    }else{
      shoppingBag.addProduct(product, amountProducts);
    }
    return true;
  }
}
