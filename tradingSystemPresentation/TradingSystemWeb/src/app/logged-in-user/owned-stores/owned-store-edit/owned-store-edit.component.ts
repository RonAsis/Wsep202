import {Component, Input, OnInit} from '@angular/core';
import {ShareService} from '../../../services/share.service';
import {Store} from '../../../shared/store.model';
import {StoreService} from '../../../services/store.service';

@Component({
  selector: 'app-owned-store-edit',
  templateUrl: './owned-store-edit.component.html',
  styleUrls: ['./owned-store-edit.component.css']
})
export class OwnedStoreEditComponent implements OnInit {

  constructor(private shareService: ShareService, private storeService: StoreService) { }

  store: Store;
  loadedFeature: string;

  ngOnInit(): void {
    this.store = this.shareService.storeSelected;
    this.loadedFeature = 'Edit-Product';
  }

  onEditProduct() {
    this.loadedFeature = 'Edit-Product';
  }

  onDiscount() {
    this.loadedFeature = 'Discount';
  }

  onManagers() {
    this.loadedFeature = 'Managers';
  }

  onOwners() {
    this.loadedFeature = 'Owners';
  }

  onPolicy() {
    this.loadedFeature = 'Policy';
  }
}
