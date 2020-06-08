import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Store} from '../shared/store.model';
import {ShoppingCart} from '../shared/shoppingCart.model';
import {PaymentDetails} from '../shared/paymentDetails.model';
import {BillingAddress} from '../shared/billingAddress.model';
import {Product} from '../shared/product.model';
import {Receipt} from '../shared/receipt.model';
import {ShoppingBag} from '../shared/ShoppingBag.model';
import {UserSystem} from '../shared/userSystem.model';
import {Manager} from '../shared/manager.model';
import {ProductShoppingCartDto} from '../shared/productShoppingCartDto.model';
import {Discount} from '../shared/discount.model';
import {OwnerToApprove} from '../shared/ownerToApprove.model';
import {DailyVistorDto} from '../shared/dailyVistor.model';

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

  public registerUser(username: string, password: string, firstName: string, lastName: string, image: File) {
    const url = `${this.guestUrl}/` + 'register-user/' +
      `${username}/` +
      `${password}/` +
      `${firstName}/` +
      `${lastName}`;
    return this.http.post<boolean>(
      url, this.getImageHttpFormat(image));
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
    const shoppingCartDtoConv = this.convertShoppingCartToJsonObject(shoppingCart);
    const url = `${this.guestUrl}/` + 'purchase-shopping-cart-guest/' ;
    return this.http.post<Receipt[] >(url,
      {shoppingCartDto: shoppingCartDtoConv, paymentDetailsDto: paymentDetails, billingAddressDto: billingAddress});
  }

  // use for products info
  public getCategories() {
    const url = `${this.guestUrl}/` + 'get-categories/';
    return this.http.get<string[]>(
      url);
  }

  getTotalPriceOfShoppingCart(shoppingCart: ShoppingCart) {
    console.log(shoppingCart);
    const convMap = this.convertShoppingCartToJsonObject(shoppingCart);
    console.log(convMap);
    const url = `${this.guestUrl}/` + 'get-total-price-of-shopping-cart/';
    return this.http.post<{key: number, value: number}>(
      url, convMap);
  }

  private convertShoppingCartToJsonObject(shoppingCart: ShoppingCart) {
    const convMap = {};
    shoppingCart.shoppingBags.forEach((val: ShoppingBag, key: number) => {
      const convShoppingBag = {};
      val.productListFromStore
        .forEach((valShoppingBag: number, keyShoppingBag: Product) => {
          convShoppingBag[keyShoppingBag.productSn] = valShoppingBag;
        });
      convMap[key] = convShoppingBag;
    });
    return convMap;
  }

//////////////////////////// BuyerRegisteredController ///////////////////////////

  public logout(username: string, uuid: string) {
    const url = `${this.buyerUrl}/` + 'logout/' +
      `${username}/` +
      `${uuid}`;
    return this.http.put<boolean>(
      url, null);
  }

  public openStore(usernameOwner: string, storeName: string, description: string, uuid: string) {
    const url = `${this.buyerUrl}/` + 'open-store/' +
      `${usernameOwner}/` +
      `${storeName}/` +
      `${description}/` +
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


  changeProductAmountInShoppingBag(username: string, storeId: number, amount: number, productSn: number, uuid: string) {
    const url = `${this.buyerUrl}/` + 'change-product-amount-in-shopping-bag/' +
      `${username}/` +
      `${storeId}/` +
      `${amount}/` +
      `${productSn}/` +
      `${uuid}`;
    return this.http.post<boolean>(
      url, null);
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
    return this.http.post<Receipt[]>(url,
      {paymentDetailsDto: paymentDetails, billingAddressDto: billingAddress});
  }

  public addProductToShoppingCart(username: string,
                                  product: Product,
                                  amount: number,
                                  uuid: string){
    const url = `${this.buyerUrl}/` + 'add-product-to-shopping-cart/' +
      `${username}/` +
      `${amount}/` +
      `${uuid}`;
    return this.http.post<boolean>(url, product);
  }

  getShoppingCart(username: string, uuid: string) {
    const url = `${this.buyerUrl}/` + 'get-shopping-cart/' +
      `${username}/` +
      `${uuid}`;
    return this.http.get<ProductShoppingCartDto[]>(url);
  }

  getTotalPriceOfShoppingCartLoggingUser(username: string, uuid: string) {
    const url = `${this.buyerUrl}/` + 'get-total-price-of-shopping-cart/' +
      `${username}/` +
      `${uuid}`;
    return this.http.get<{key: number, value: number}>(
      url );
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

  getUsers(administratorUsername: string, uuid: string) {
    const url = `${this.adminUrl}/` + 'get-users/' +
      `${administratorUsername}/` +
      `${uuid}`;
    return this.http.get<UserSystem[]>(
      url);
  }

  getDailyVisitors(administratorUsername: string, startDate: Date, endDate: Date, uuid: string) {
    const url = `${this.adminUrl}/` + 'get-daily-visitors/' +
      `${administratorUsername}/` +
      `${uuid}`;
    return this.http.post<DailyVistorDto[]>(
      url, {start: startDate, end: endDate});
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

  getMyPermissions(manageUsername: string, storeId: number, uuid: string){
    const url = `${this.sellerManagerUrl}/` + 'get-operations-can-do/' +
      `${manageUsername}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<string[]>(
      url);
  }

  getStoreDiscounts(username: string, storeId: number, uuid: string) {
    const url = `${this.sellerManagerUrl}/` + 'get-store-discounts/' +
      `${username}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<Discount[]>(
      url);
  }

  removeDiscount(username: string, storeId: number, discountId: number, uuid: string) {
    const url = `${this.sellerManagerUrl}/` + 'remove-discount/' +
      `${username}/` +
      `${storeId}/` +
      `${discountId}/` +
      `${uuid}`;
    return this.http.post<boolean>(
      url, null);
  }


  getCompositeOperators(username: string, storeId: number, uuid: string) {
    const url = `${this.sellerManagerUrl}/` + 'get-composite-operators/' +
      `${username}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<string[]>(
      url);
  }
  getAllDiscounts(username: string, storeId: number, uuid: string) {
    const url = `${this.sellerManagerUrl}/` + 'get-discounts/' +
      `${username}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<Discount[]>(
      url);
  }

  getSimpleDiscounts(username: string, storeId: number, uuid: string) {
    const url = `${this.sellerManagerUrl}/` + 'get-simple-discounts/' +
      `${username}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<Discount[]>(
      url);
  }

  addDiscount(username: string, storeId: number, discount: Discount, uuid: string) {
    const url = `${this.sellerManagerUrl}/` + 'add-discount/' +
      `${username}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.post<Discount>(
      url, discount);
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

  public addProduct(ownerUsername: string, storeId: number, productName: string,
                    category: string, amount: number, cost: number, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'add-product/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${productName}/` +
      `${category}/` +
      `${amount}/` +
      `${cost}/` +
      `${uuid}`;
    return this.http.post<Product>(
      url, null);
  }

  public deleteProductFromStore(ownerUsername: string, storeId: number, productSn: number, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'delete-product-from-store/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${productSn}/` +
      `${uuid}`;
    return this.http.put<boolean>(
      url, null);
  }

  public editProduct(ownerUsername: string, storeId: number, productSn: number, productName: string,
                     category: string, amount: number, cost: number, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'edit-product/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${productSn}/` +
      `${productName}/` +
      `${category}/` +
      `${amount}/` +
      `${cost}/` +
      `${uuid}`;
    return this.http.put<boolean>(
      url, null);
  }

  public addOwner(ownerUsername: string, storeId: number, newOwnerUsername: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'add-owner/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${newOwnerUsername}/` +
      `${uuid}`;
    return this.http.post<boolean>(
      url, null);
  }

  public getAllUsernameNotOwnerNotManger(ownerUsername: string, storeId: number, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'get-user-not-manger-not-owner/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<string[]>(
      url);
  }

  public removeManager(ownerUsername: string, storeId: number, managerUsername: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'remove-manager/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${managerUsername}/` +
      `${uuid}`;
    return this.http.post<boolean>(
      url, null);
  }

  public addPermission(ownerUsername: string, storeId: number, managerUsername: string, permission: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'add-permission/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${managerUsername}/` +
      `${permission}/` +
      `${uuid}`;
    return this.http.put<boolean>(
      url, null);
  }

  public addManager(ownerUsername: string, storeId: number, newManagerUsername: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'add-manager/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${newManagerUsername}/` +
      `${uuid}`;
    return this.http.post<Manager>(
      url, null);
  }

  public getOwnerStores(ownerUsername: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'get-owner-stores/' +
      `${ownerUsername}/` +
      `${uuid}`;
    return this.http.get<Store[]>(
      url);
  }

  public getAllOperationOfManger() {
    const url = `${this.sellerOwnerUrl}/` + 'get-all-operation-manager/';
    return this.http.get<Store[]>(
      url);
  }

  getMySubOwners(ownerUsername: string, storeId: number, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'get-my-sub-owners/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<string[]>(
      url);
  }

  getMySubMangers(ownerUsername: string, storeId: number, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'get-my-sub-managers/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<Manager[]>(
      url);
  }


  removePermission(ownerUsername: string, storeId: number, managerUsername: string,
                   permission: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'remove-permission/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${managerUsername}/` +
      `${permission}/` +
      `${uuid}`;
    return this.http.put<boolean>(
      url, null);
  }


  getPermissionCantDo(ownerUsername: string, storeId: number, managerUsername: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'get-permissions-cant-do/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${managerUsername}/` +
      `${uuid}`;
    return this.http.get<string[]>(
      url);
  }


  isOwner(username: string, storeId: number, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'is-owner/' +
      `${username}/` +
      `${storeId}/` +
      `${uuid}`;
    return this.http.get<boolean>(
      url);
  }

  public removeOwner(ownerUsername: string, storeId: number, ownerToRemove: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'remove-owner/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${ownerToRemove}/` +
      `${uuid}`;
    return this.http.post<Manager>(
      url, null);
  }

  public approveOwner(ownerUsername: string, storeId: number, ownerToApprove: string, status: boolean, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'approve-owner/' +
      `${ownerUsername}/` +
      `${storeId}/` +
      `${ownerToApprove}/` +
      `${status}/` +
      `${uuid}`;
    return this.http.post<boolean>(
      url, null);
  }

  public getMyOwnerToApprove(ownerUsername: string, uuid: string) {
    const url = `${this.sellerOwnerUrl}/` + 'get-my-owner-to-approve/' +
      `${ownerUsername}/` +
      `${uuid}`;
    return this.http.get<OwnerToApprove[]>(
      url);
  }
  //////////////////////////////////////// general /////////////////////////////

  private getImageHttpFormat(image: File){
    const uploadImageData = new FormData();
    if (image !== null && image !== undefined) {
      console.log('file define');
      uploadImageData.append('imageFile', image, image.name);
    }
    return uploadImageData;
  }


}
