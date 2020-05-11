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
  }

  getIsAdminOwnerManger() {
     return this.storeService.getOwnerStores();
  }

  editStore() {
    this.shareService.storeSelected = this.store;
    this.shareService.featureSelected.emit('Edit-Store');
  }
}
