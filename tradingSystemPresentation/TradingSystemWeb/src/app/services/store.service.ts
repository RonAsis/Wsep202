import {EventEmitter, Injectable, Input} from '@angular/core';
import {Store} from '../shared/store.model';
import {HttpClient} from '@angular/common/http';
import {Product} from '../shared/product.model';
import {Receipt} from '../shared/receipt.model';
import {HttpService} from './http.service';
import {UserService} from './user.service';

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  storeSelected = new EventEmitter<Store>();

  // for know which stores get
  private ownerStore = false;
  private mangerStore = false;

  constructor(private httpService: HttpService, private userService: UserService) {
  }

  public setOwnerStores(ownerStore: boolean) {
    this.ownerStore = ownerStore;
  }

  public getOwnerStores() {
    return this.ownerStore;
  }

  public setManagerStores(mangerStore: boolean) {
    this.mangerStore = mangerStore;
  }

  public getManagerStores() {
    return this.mangerStore;
  }

  getStores(username: string, uuid: string) {
    if (this.ownerStore) {
      console.log('owner stores');
      return this.httpService.getOwnerStores(username, uuid);
    } else if (this.mangerStore) {
      console.log('manager stores');
      return this.httpService.getManageStores(username, uuid);
    } else {
      console.log('get stores');
      return this.httpService.getStores();
    }
  }

  viewPurchaseHistory(storeId: number) {
    if (this.ownerStore) {
      return this.httpService
        .viewPurchaseHistoryOfOwner(this.userService.getUsername(), storeId, this.userService.getUuid());
    } else if (this.ownerStore) {
      return this.httpService
        .viewPurchaseHistoryOfManager(this.userService.getUsername(), storeId, this.userService.getUuid());
    }
  }

  openStore(storeName: string, description: string) {
    return this.httpService.openStore(this.userService.getUsername(), storeName, description, this.userService.getUuid());
  }

  addProduct(storeId: number, productName: string, category: string, amount: number, cost: number) {
    return this.httpService.addProduct(this.userService.getUsername(), storeId, productName, category,
      amount, cost, this.userService.getUuid());
  }

  deleteProductFromStore(productSn: number, storeId: number) {
    return this.httpService.deleteProductFromStore(this.userService.getUsername(),
      storeId, productSn, this.userService.getUuid());
  }

  editProduct(storeId: any, productSn: number, productName: string, category: string, amount: number, cost: number) {
    return this.httpService.editProduct(this.userService.getUsername(), storeId, productSn, productName, category,
      amount, cost, this.userService.getUuid());
  }

  public getAllUsernameNotOwnerNotManger( storeId: number) {
    return this.httpService.getAllUsernameNotOwnerNotManger(this.userService.getUsername(),
      storeId, this.userService.getUuid());
  }

  addOwner(storeId: number, selectedNewOwner: string) {
    return this.httpService.addOwner(this.userService.getUsername(),
      storeId, selectedNewOwner, this.userService.getUuid());
  }

  getMySubOwners(storeId: number) {
    return this.httpService.getMySubOwners(this.userService.getUsername(),
      storeId, this.userService.getUuid());
  }

  addManager(storeId: number, selectedNewManager: string) {
    return this.httpService.addManager(this.userService.getUsername(),
      storeId, selectedNewManager, this.userService.getUuid());
  }

  getMySubMangers(storeId: number) {
    return this.httpService.getMySubMangers(this.userService.getUsername(),
      storeId, this.userService.getUuid());
  }
}
