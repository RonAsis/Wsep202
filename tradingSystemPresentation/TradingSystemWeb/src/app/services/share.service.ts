import {EventEmitter, Injectable, Output} from '@angular/core';
import {Receipt} from '../shared/receipt.model';

@Injectable({
  providedIn: 'root'
})
export class ShareService {
  featureSelected = new EventEmitter<string>();
  receipts: Receipt[];
  constructor() {
    this.receipts = [];
  }

  getReceipts(receipts: Receipt[]) {
    this.receipts = receipts;
  }

}
