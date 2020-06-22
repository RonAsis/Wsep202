import {Component, Input, OnInit} from '@angular/core';
import {ShareService} from '../../../services/share.service';
import {Store} from '../../../shared/store.model';
import {StoreService} from '../../../services/store.service';
import {UserService} from '../../../services/user.service';
import {Discount} from '../../../shared/discount.model';

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
  permissions: string [];

  ngOnInit(): void {
    this.store = this.shareService.storeSelected;
    this.loadedFeature = '';
    this.storeService.getIsOwner(this.store.storeId).subscribe(response => {
      if (response){
        this.isOwner = true;
        this.loadedFeature = 'Edit-Product';
      }else{
        this.storeService.getMyPermissions(this.store.storeId).subscribe(response1 => {
          if ( response1 !== undefined){
            this.permissions = response1;
            this.loadedFeature = this.permissions.includes('Edit-Product') ? 'Edit-Product' : this.permissions[0];
          }
        });
      }
    });
  }

  onEditProduct() {
    this.loadedFeature = 'Edit-Product';
  }

  onAddDiscount() {
    this.loadedFeature = 'Add Discount';
  }

  onAddPolicy() {
    this.loadedFeature = 'Add Policy';
  }

  onManagers() {
    this.loadedFeature = 'Managers';
  }

  onOwners() {
    this.loadedFeature = 'Owners';
  }

  onPolicies() {
    this.loadedFeature = 'Policies';
  }

  onDiscounts() {
    this.loadedFeature = 'Discounts';
  }

  discountAdded($event: Discount) {
    this.onDiscounts();
  }

  policyAdded($event: any) {
    this.onPolicies();
  }

  isManagerCanEditDiscount() {
    return this.permissions !== undefined && this.permissions.includes('edit discount');
  }

  isManagerCanEditProduct() {
    return this.permissions !== undefined && this.permissions.includes('edit product');
  }

  isManagerCanEditPurchasePolicy() {
    return this.permissions !== undefined && this.permissions.includes('edit purchase policy');
  }

  isManagerCanEditManager() {
    return this.permissions !== undefined && this.permissions.includes('edit managers');
  }
}
