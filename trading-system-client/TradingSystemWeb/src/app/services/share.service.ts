import {EventEmitter, Injectable, Output} from '@angular/core';
import {Receipt} from '../shared/receipt.model';
import {Store} from '../shared/store.model';
import {ProductShoppingCartDto} from '../shared/productShoppingCartDto.model';
import {ShoppingCart} from '../shared/shoppingCart.model';
import {ShoppingBag} from '../shared/ShoppingBag.model';
import {Product} from '../shared/product.model';

@Injectable({
  providedIn: 'root'
})
export class ShareService {
  username: string;
  basicAuthenticationHttpHeader: string;
  featureSelected = new EventEmitter<string>();
  receipts: Receipt[];
  storeSelected: Store;
  userSelected: string;
  constructor() {
    this.receipts = [];
  }

  setReceipts(receipts: Receipt[]) {
    this.receipts = receipts;
  }

  createShoppingCart(productShoppingCartDtos: ProductShoppingCartDto[]){
    const shoppingCart = new ShoppingCart(new Map<number, ShoppingBag>());
    productShoppingCartDtos.forEach(productShoppingCartDto => {
      const product = new Product(productShoppingCartDto.productSn,
        productShoppingCartDto.name,
        '',
        productShoppingCartDto.amountInShoppingCart,
        productShoppingCartDto.originalCost,
        5,
        productShoppingCartDto.storeId,
        productShoppingCartDto.cost,
        '');
      shoppingCart.addToShoppingCart(product, productShoppingCartDto.amountInShoppingCart);
    });
    return shoppingCart;
  }

  createShoppingCartItems(shoppingCart: ShoppingCart) {
    return Array.from(shoppingCart.shoppingBags.values())
      .reduce((acc, cur) => {
        cur.productListFromStore.forEach((amountInShoppingCart, product) =>
          acc.push(new ProductShoppingCartDto(product.productSn,
            product.name, amountInShoppingCart, product.cost,  product.costAfterDiscount, product.storeId)));
        return acc;
      }, []);
  }
}
