import {AfterViewInit, Component, Input, OnInit, ViewChild, ViewChildren} from '@angular/core';
import {Store} from '../../../shared/store.model';
import {ProductsComponent} from '../../products/products.component';
import {ProductListComponent} from '../../products/product-list/product-list.component';
import {UserService} from '../../../services/user.service';
import {StoreService} from '../../../services/store.service';
import {ShareService} from '../../../services/share.service';

@Component({
  selector: 'app-store-detail',
  templateUrl: './store-detail.component.html',
  styleUrls: ['./store-detail.component.css']
})
export class StoreDetailComponent implements OnInit, AfterViewInit{
  @Input()store: Store;
  managerWithPermissionEdit = false;
  managerWithPermissionView = false;
  constructor(private userService: UserService, private storeService: StoreService,
              private shareService: ShareService) { }

  ngOnInit(): void {
    this.setPermissionForManager();
  }

  ngAfterViewInit(): void {
  }

  viewPurchaseHistory() {
    this.shareService.storeSelected = this.store;
    this.shareService.featureSelected.emit('History-purchase');
  }

  isOwner() {
     return this.storeService.getOwnerStores();
  }

  isManagerWithPermissionEdit() {
    return this.managerWithPermissionEdit;
  }

   setPermissionForManager() {
    if (this.storeService.getManagerStores()) {
      this.storeService.getMyPermissions(this.store.storeId)
        .subscribe((response: string[]) => {
          console.log(response);
          if (response !== null && response !== undefined){
            this.managerWithPermissionEdit = response.includes('edit');
            this.managerWithPermissionView = response.includes('view');
          }
        });
    }
  }

  isManagerWithPermissionViewOrAdminOrOwner() {
    return this.userService.getIsAdmin() || this.isOwner() || this.managerWithPermissionView;
  }

  editStore() {
    this.shareService.storeSelected = this.store;
    this.shareService.featureSelected.emit('Edit-Store');
  }

  editStoreManager() {
    this.shareService.storeSelected = this.store;
    this.shareService.featureSelected.emit('Edit-Store-Manager');
  }
}
