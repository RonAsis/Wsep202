import {Component, Input, OnInit} from '@angular/core';
import {ShareService} from '../../../services/share.service';
import {Store} from '../../../shared/store.model';
import {StoreService} from '../../../services/store.service';
import {UserService} from '../../../services/user.service';

@Component({
  selector: 'app-owned-store-edit',
  templateUrl: './owned-store-edit.component.html',
  styleUrls: ['./owned-store-edit.component.css']
})
export class OwnedStoreEditComponent implements OnInit {

  constructor(private shareService: ShareService, private storeService: StoreService,
              private userService: UserService) { }

  store: Store;
  loadedFeature: string;
  isOwner = false;

  ngOnInit(): void {
    this.store = this.shareService.storeSelected;
    this.loadedFeature = 'Edit-Product';
    this.storeService.getIsOwner(this.store.storeId).subscribe(response =>{
      if (response){
        this.isOwner = true;
      }
    });
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
