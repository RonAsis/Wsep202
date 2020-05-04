import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Store} from '../shared/store.model';
import {ShoppingCart} from '../shared/shoppingCart.model';
import {PaymentDetails} from '../shared/paymentDetails.model';
import {BillingAddress} from '../shared/billingAddress.model';
import {Product} from '../shared/product.model';
import {Receipt} from '../shared/receipt.model';

@Injectable({
  providedIn: 'root'
})
export class HttpService {
  // server
  private readonly serverUrl: string;
  // services urls
  private readonly guestUrl: string;
  private readonly buyerUrl: string;
  private readonly adminUrl: string;
  private readonly sellerManagerUrl: string;
  private readonly sellerOwnerUrl: string;

  constructor(private http: HttpClient) {
    // initial server url
    this.serverUrl = 'http://localhost:8080';
    // initial urls of services
    this.guestUrl = this.serverUrl + '/guest';
    this.buyerUrl = this.serverUrl + '/buyer-reg';
    this.adminUrl = this.serverUrl + '/admin';
    this.sellerManagerUrl = this.serverUrl + '/seller-manager';
    this.sellerOwnerUrl = this.serverUrl + '/seller-owner';
  }

  //////////////////////////// GuestController ///////////////////////////

  public registerUser(username: string, password: string, firstName: string, lastName: string) {
    const url = `${this.guestUrl}/` + 'register-user/' +
      `${username}/` +
      `${password}/` +
      `${firstName}/` +
      `${lastName}`;
    return this.http.post<boolean>(
      url, null);
  }

  public login(username: string, password: string) {
    const url = `${this.guestUrl}/` + 'login/' +
      `${username}/` +
      `${password}`;
    return this.http.put<{ key: string; value: boolean }>(
      url, null);
  }

  // use for store info
  public getStores() {
    const url = `${this.guestUrl}/` + 'get-stores/';
    return this.http.get<Store[]>(
      url);
  }

  // use for products info
  public getProducts() {
    const url = `${this.guestUrl}/` + 'get-products/';
    return this.http.get<Product[]>(
      url);
  }

  public purchaseShoppingCartGuest(shoppingCart: ShoppingCart,
                                   paymentDetails: PaymentDetails,
                                   billingAddress: BillingAddress) {
    const url = `${this.guestUrl}/` + 'purchase-shopping-cart-guest/';
    return this.http.post(url,
      {shoppingCartDto: shoppingCart, paymentDetailsDto: paymentDetails, billingAddressDto: billingAddress});
  }

  // use for products info
  public getCategories() {
    const url = `${this.guestUrl}/` + 'get-categories/';
    return this.http.get<string[]>(
      url);
  }

  //////////////////////////// BuyerRegisteredController ///////////////////////////

  public logout(username: string, uuid: string) {
    const url = `${this.buyerUrl}/` + 'logout/' +
      `${username}/` +
      `${uuid}`;
    return this.http.put<boolean>(
      url, null);
  }

  public openStore(usernameOwner: string, storeName: string, uuid: string) {
    const url = `${this.buyerUrl}/` + 'open-store/' +
      `${usernameOwner}/` +
      `${storeName}/` +
      `${uuid}/`;
    return this.http.post<boolean>(
      url, null);
  }

  public viewPurchaseHistory(username: string, uuid: string) {
    const url = `${this.buyerUrl}/` + 'view-purchase-history/' +
      `${username}/` +
      `${uuid}`;
    return this.http.get<Receipt[]>(
      url);
  }

  public saveProductInShoppingBag(username: string, storeId: number, productSn: number,
                                  amount: number, uuid: string) {
    const url = `${this.buyerUrl}/` + 'save-product-in-shopping-bag/' +
      `${username}/` +
      `${storeId}/` +
      `${productSn}/` +
      `${amount}/` +
      `${uuid}`;
    return this.http.post<boolean>(
      url, null);
  }

  public viewProductsInShoppingCart(username: string, uuid: string) {
    const url = `${this.buyerUrl}/` + 'view-products-in-shopping-cart/' +
      `${username}/` +
      `${uuid}`;
    return this.http.get<Receipt[]>(
      url);
  }

  public removeProductInShoppingBag(username: string, storeId: number, productSn: number, uuid: string) {
    const url = `${this.buyerUrl}/` + 'remove-product-in-shopping-bag/' +
      `${username}/` +
      `${storeId}/` +
      `${productSn}/` +
      `${uuid}`;
    return this.http.post<boolean>(
      url, null);
  }

  public purchaseShoppingCartBuyer(username: string,
                                   paymentDetails: PaymentDetails,
                                   billingAddress: BillingAddress,
                                   uuid: string) {
    const url = `${this.buyerUrl}/` + 'purchase-shopping-cart-buyer/' +
      `${username}/` +
      `${uuid}`;
    return this.http.post(url,
      {paymentDetailsDto: paymentDetails, billingAddressDto: billingAddress});
  }

  //////////////////////////// AdministratorController ///////////////////////////

  public viewPurchaseHistoryStoreByAdmin(administratorUsername: string, storeId: number, uuid: string) {
    const url = `${this.adminUrl}/` + 'view-purchase-history-store/' +
      `${administratorUsername}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<Receipt[]>(
      url);
  }

  public viewPurchaseHistoryUserByAdmin(administratorUsername: string, username: string, uuid: string) {
    const url = `${this.adminUrl}/` + 'view-purchase-history-user/' +
      `${administratorUsername}/` +
      `${username}/` +
      `${uuid}`;
    return this.http.get<Receipt[]>(
      url);
  }

  //////////////////////////// SellerManagerController ///////////////////////////

  public viewPurchaseHistoryOfManager(username: string, storeId: number, uuid: string) {
    const url = `${this.sellerManagerUrl}/` + 'view-purchase-history-of-manager/' +
      `${username}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<Receipt[]>(
      url);
  }

  public getManageStores(manageUsername: string, uuid: string) {
    const url = `${this.sellerManagerUrl}/` + 'get-manage-stores/' +
      `${manageUsername}/` +
      `${uuid}`;
    return this.http.get<Store[]>(
      url);
  }

  public getOperationsCanDo(manageUsername: string, storeId: string, uuid: string){
    const url = `${this.sellerManagerUrl}/` + 'get-operations-can-do/' +
      `${manageUsername}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<string[]>(
      url);
  }

  //////////////////////////// SellerOwnerController ///////////////////////////

  public viewPurchaseHistoryOfOwner(ownerUsername: string, storeId: number, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'view-purchase-history-of-owner/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<Receipt[]>(
      url);
  }

}
