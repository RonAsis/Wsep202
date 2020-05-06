import {EventEmitter, Injectable, Input} from '@angular/core';
import {Store} from '../shared/store.model';
import {HttpClient} from '@angular/common/http';
import {Product} from '../shared/product.model';
import {Receipt} from '../shared/receipt.model';
import {HttpService} from './http.service';

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  storeSelected = new EventEmitter<Store>();

  // for know which stores get
  private ownerStore = false;
  private mangerStore = false;

  private storeWantViewPurchaseHistory: number;

  constructor(private httpService: HttpService) {
  }

  public setOwnerStores(ownerStore: boolean) {
    this.ownerStore = ownerStore;
  }

  public setManagerStores(mangerStore: boolean) {
    this.mangerStore = mangerStore;
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

  wantViewPurchaseHistory(store: Store) {
    this.storeWantViewPurchaseHistory = store.storeId;
  }

  viewPurchaseHistory() {
    const map = new Map();
    map.set(new Product(1, 'store !!!!1', 'sds', 1, 3232, 323, 1, 1, 'sdsd'), 3);
    map.set(new Product(1, 'store !!!!', 'sdsdsdds', 1, 232, 323, 1, 1, 'sdsd'), 3);

    return [new Receipt(1, 1, 'store!!!!!!', new Date(), 2, map)];
  }
}
