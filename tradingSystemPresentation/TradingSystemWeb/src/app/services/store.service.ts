import {EventEmitter, Injectable, Input} from '@angular/core';
import {Store} from '../shared/store.model';
import {HttpClient} from '@angular/common/http';
import {Product} from '../shared/product.model';
import {Receipt} from '../shared/receipt.model';

@Injectable({
  providedIn: 'root'
})
export class StoreService {

  storeSelected = new EventEmitter<Store>();
  private ownerStore = false;
  private mangerStore = false;

  private readonly ownerStoreUrl: string;
  private readonly mangerStoreUrl: string;
  private readonly guestUrl: string;
  private storeWantViewPurchaseHistory: number;

  constructor(private http: HttpClient) {
    this.ownerStoreUrl = 'http://localhost:8080/seller-owner';
    this.mangerStoreUrl = 'http://localhost:8080/seller-manager';
    this.guestUrl = 'http://localhost:8080/guest';
  }

  public setOwnerStores(ownerStore: boolean) {
    this.ownerStore = ownerStore;
  }

  public setManagerStores(mangerStore: boolean) {
    this.mangerStore = mangerStore;
  }

  getStores(username: string, uuid: string) {
    console.log('get stores bebugger');
    let stores: Store[];
    if (this.ownerStore) {
      const urlGetStoresOwner = `${this.ownerStoreUrl}/` +
        'get-owner-stores/' +
        `${(username)}/` +
        `${(uuid)}`;
      this.http.get<Store[]>(
        urlGetStoresOwner).subscribe(res => stores = res);
    } else if (this.mangerStore) {
      const urlGetStoresManager = `${this.mangerStoreUrl}/` +
        'get-manage-stores/' +
        `${(username)}/` +
        `${(uuid)}`;
      this.http.get<Store[]>(
        urlGetStoresManager).subscribe(res => stores = res);
    } else {
      const urlGetStoresOwner = `${this.guestUrl}/` +
        'get-stores/';
      this.http.get<Store[]>(
        urlGetStoresOwner).subscribe(res => stores = res);
    }
    // return stores;
    return [
      new Store(1, 'storename', [], 5),
      new Store(1, 'storename', [], 9)
    ];
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
