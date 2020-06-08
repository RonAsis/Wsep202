import {Product} from './product.model';

export class ShoppingBag {
  constructor(public productListFromStore: Map<Product, number>) {
  }
  addProductAmounts(productSn: number, amount: number) {
    let product: Product = null;
    this.productListFromStore.forEach(((value, key) => {
      if (key.productSn === productSn) {
        product = key;
      }
    }));
    this.productListFromStore.set(product , amount);
  }

  removeCartItem(productSn: number) {
    let product: Product = null;
    this.productListFromStore.forEach(((value, key) => {
      if (key.productSn === productSn) {
        product = key;
      }
    }));
    this.productListFromStore.delete(product);
  }

  addProduct(product: Product, amountProducts: number) {
    const productFind = Array.from(this.productListFromStore.keys())
      .filter((productCur) => productCur.productSn === product.productSn)
      .pop();
    if (productFind !== undefined){
      console.log(this.productListFromStore.get(productFind));
      console.log(amountProducts);
      const amount = +this.productListFromStore.get(productFind) + +amountProducts;
      console.log(amount);
      this.productListFromStore.set(productFind, amount);
    }else{
      this.productListFromStore.set(product, amountProducts);
    }
  }

}
