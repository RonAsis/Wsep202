import {EventEmitter, Injectable, Output} from '@angular/core';
import {Receipt} from '../shared/receipt.model';
import {Store} from '../shared/store.model';

@Injectable({
  providedIn: 'root'
})
export class ShareService {
  featureSelected = new EventEmitter<string>();
  receipts: Receipt[];
  storeSelected: Store;
  constructor() {
    this.receipts = [];
  }

  getReceipts(receipts: Receipt[]) {
    this.receipts = receipts;
  }

}
