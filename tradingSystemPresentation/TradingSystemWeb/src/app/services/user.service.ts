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

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private shoppingCart: ShoppingCart;
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

  constructor(private httpService: HttpService) {
    this.shoppingCart = new ShoppingCart(new Map<number, ShoppingBag>());
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
        }else{
          this.registerEvent.emit(false);
        }
      }, error => this.registerEvent.emit(false));
  }

  login(username: string, password: string) {
    this.usernameWantSeeHistory = null; // need to remove from here
    this.httpService.login(username, password).subscribe(
      response => {
        if (response !== null && response.key !== null ) {
          this.uuid = response.key;
          this.isAdmin = response.value;
          this.username = username;
          this.userLoggingEvent.emit(true);
        }else{
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
        }else{
          this.userLogoutEvent.emit(false);
        }
      });
  }

  addToShoppingCart(product: Product, amountProducts: number) {
    const storeId = product.storeId;
    let shoppingBag = this.shoppingCart.shoppingBags.get(storeId);
    if (shoppingBag === undefined) {
      const products = new Map<Product, number>();
      products.set(product, amountProducts);
      shoppingBag = new ShoppingBag(products);
    }
    this.shoppingCart.shoppingBags.set(storeId, shoppingBag);
  }

  viewPurchaseHistory() {
    let receipts: Receipt[] = [];
    this.httpService.viewPurchaseHistory(this.username, this.uuid)
      .subscribe(res => receipts = res);

    ///// need to delete///////
    const map = new Map();
    map.set(new Product(1, '2', 'sds', 1, 3232, 323, 1, 1, 'sdsd'), 3);
    map.set(new Product(1, '2', 'sdsdsdds', 1, 232, 323, 1, 1, 'sdsd'), 3);

    return [new Receipt(1, 1, 'sds', new Date(), 2, map)];
    ///////////////

    // return receipts;
  }

  getUsers() {
    console.log(this.usernameWantSeeHistory);
    if (this.usernameWantSeeHistory !== null) {
      return [new UserSystem('ron', 'ron', 'asis'),
        new UserSystem('ron1', 'ro1n11', 'asis1')];
    } else {
      return [new UserSystem('null', 'null', 'null'),
        new UserSystem('null', 'null', 'null')];
    }
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
}
