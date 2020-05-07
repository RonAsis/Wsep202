import {Component, OnInit, EventEmitter, Output, Input} from '@angular/core';
import {Product} from '../../shared/product.model';
import {UserService} from '../../services/user.service';
import {ShoppingCart} from '../../shared/shoppingCart.model';
import {Observable} from 'rxjs';
import {ShareService} from '../../services/share.service';

@Component({
  selector: 'app-shopping-cart',
  templateUrl: './shopping-cart.component.html',
  styleUrls: ['./shopping-cart.component.css']
})
export class ShoppingCartComponent implements OnInit {

  @Input() cartTotal: number;
  @Input() cartTotalAfterDiscount: number;
  @Input() cartItems: Map<Product , number> ;

  @Output() cartItemDeleted = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();
  @Output() cartItemChanged = new EventEmitter<{
    productSn: number,
    storeId: number
  }>();
  constructor(private userService: UserService, private shareService: ShareService) { }

  ngOnInit(): void {
    this.getShoppingCart();
    this.getTotalPrice();
    }

  private getShoppingCart() {
    this.userService.getShoppingCart().subscribe((shoppingCart) => {
      if (shoppingCart !== null && shoppingCart !== undefined) {
        this.cartItems = Array.from(shoppingCart.shoppingBags.values())
          .reduce((acc, cur) => {
            cur.productListFromStore.forEach((value, key) => acc.set(key, value));
            return acc;
          }, new Map<Product, number>());
      }
    });
  }

  onCartItemDeleted(productData: { productSn: number, storeId: number }) {
    this.userService.removeCartItem(productData.productSn, productData.storeId);
    this.getShoppingCart();
    this.getTotalPrice();
  }

  private getTotalPrice() {
    this.userService.getTotalPriceOfShoppingCart().subscribe(prices => {
      this.cartTotal = prices.key;
      this.cartTotalAfterDiscount = prices.value;
    });
  }

  onCartItemChanged(productData: { productSn: number, storeId: number }) {
    this.cartItemChanged.emit({
      productSn: productData.productSn,
      storeId: productData.storeId
    });
    this.getTotalPrice();
  }

  onPurchaseShoppingCart() {
    this.shareService.featureSelected.emit('Purchase-shopping-cart');
  }
}
