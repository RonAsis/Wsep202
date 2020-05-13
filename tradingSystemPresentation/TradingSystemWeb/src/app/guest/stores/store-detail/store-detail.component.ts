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
  wantViewPurchaseHistory: boolean;

  constructor(private userService: UserService, private storeService: StoreService,
              private shareService: ShareService) { }

  ngOnInit(): void {
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
    return this.isHasPermission('edit');
  }

  private isHasPermission(permission: string) {
    let res = false;
    if (this.storeService.getManagerStores()) {
      this.storeService.getMyPermissions(this.store.storeId)
        .subscribe((response: string[]) => res = response.includes(permission));
    }
    return res;
  }

  isManagerWithPermissionViewOrAdminOrOwner() {
    return this.userService.getIsAdmin() || this.isOwner() || this.isHasPermission('view');
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
