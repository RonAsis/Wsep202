import {EventEmitter, Injectable, Output} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {UserSystem} from '../shared/userSystem.model';
import {Product} from '../shared/product.model';
import {ShoppingCart} from '../shared/shoppingCart.model';
import {ShoppingBag} from '../shared/shoppingBag.model';
import {formatNumber} from '@angular/common';
import {Receipt} from '../shared/receipt.model';
import {Store} from '../shared/store.model';
import {HttpService} from './http.service';
import {Observable, of} from 'rxjs';
import {PaymentDetails} from '../shared/paymentDetails.model';
import {BillingAddress} from '../shared/billingAddress.model';
import {ShareService} from './share.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly shoppingCart: ShoppingCart;
  private isAdmin: boolean;
  private uuid: string;
  private username: string;

  // events
  userLoggingEvent = new EventEmitter<boolean>();

  registerEvent = new EventEmitter<boolean>();

  userLogoutEvent = new EventEmitter<boolean>();
  logoutNoEvent = new EventEmitter<boolean>();

  userSelectedEvent = new EventEmitter<UserSystem>();

  private usernameWantSeeHistory = null;

  constructor(private httpService: HttpService, private  shareService: ShareService) {
    this.shoppingCart = new ShoppingCart(new Map<number, ShoppingBag>());
    this.isAdmin = false;
  }

  public register(username: string,
                  password: string,
                  firstName: string,
                  lastName: string,
                  image: File) {
    this.httpService.registerUser(username, password, firstName, lastName, image)
      .subscribe(response => {
        if (response !== null && response === true) {
          this.registerEvent.emit(true);
        } else {
          this.registerEvent.emit(false);
        }
      }, error => this.registerEvent.emit(false));
  }

  login(username: string, password: string) {
    this.usernameWantSeeHistory = null; // need to remove from here
    this.httpService.login(username, password).subscribe(
      response => {
        if (response !== null && response.key !== null) {
          this.uuid = response.key;
          this.isAdmin = response.value;
          this.username = username;
          this.userLoggingEvent.emit(true);
        } else {
          this.userLoggingEvent.emit(false);
        }
      });
  }

  logout() {
    this.httpService.logout(this.username, this.uuid).subscribe(
      response => {
        if (response) {
          this.uuid = null;
          this.isAdmin = false;
          this.userLogoutEvent.emit(true);
        } else {
          this.userLogoutEvent.emit(false);
        }
      });
  }

  addToShoppingCart(product: Product, amountProducts: number) {
    if (!this.isLoggingUser()) {
      return of(this.shoppingCart.addToShoppingCart(product, amountProducts));
    }else{
      return this.httpService.addProductToShoppingCart(this.username, product, amountProducts, this.uuid);
    }
  }

  viewPurchaseHistory() {
    return this.httpService.viewPurchaseHistory(this.username, this.uuid);
  }

  getUsers() {
     return this.httpService.getUsers(this.username, this.uuid);
  }

  wantViewPurchaseHistory(user: UserSystem) {
    this.usernameWantSeeHistory = user.username;
  }

  public getUsername() {
    return this.username;
  }

  public getIsAdmin() {
    return this.isAdmin;
  }

  public getUuid() {
    return this.uuid;
  }

  getShoppingCart() {
    console.log(this.isLoggingUser());
    if (!this.isLoggingUser()) {
      return of(this.shareService.createShoppingCartItems(this.shoppingCart));
    }else {
      return this.httpService.getShoppingCart(this.username, this.uuid);
    }
  }

  isLoggingUser(){
    return this.uuid !== null && this.uuid !== undefined;
  }

  changeItemCartAmount(productSn: number, storeId: number, amount: number) {
    if (!this.isLoggingUser()){
      this.shoppingCart.addProductAmounts(productSn, storeId, amount);
    }
  }

  removeCartItem(productSn: number, storeId: number) {
    if (!this.isLoggingUser()){
      return of(this.shoppingCart.removeCartItem(productSn, storeId));
    }else{
      return this.httpService.removeProductInShoppingBag(this.username, storeId, productSn, this.uuid);
    }
  }

  getTotalPriceOfShoppingCart() {
    if (!this.isLoggingUser()){
        return this.httpService.getTotalPriceOfShoppingCart(this.shoppingCart);
    }else{
      return this.httpService.getTotalPriceOfShoppingCartLoggingUser(this.username, this.uuid);
    }
  }

  purchaseShoppingCart(paymentDetails: PaymentDetails, billingAddress: BillingAddress) {
    if (!this.isLoggingUser()){
      return this.httpService.purchaseShoppingCartGuest(this.shoppingCart, paymentDetails, billingAddress);
    }else{
      return this.httpService.purchaseShoppingCartBuyer(this.username, paymentDetails, billingAddress, this.uuid);
    }
  }
}
