import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {StoreService} from '../../services/store.service';
import {WebSocketAPI} from '../../shared/apis/webSocketApi.model';
import {UserService} from '../../services/user.service';
import {ShareService} from '../../services/share.service';

@Component({
  selector: 'app-header-logging-user',
  templateUrl: './header-logging-user.component.html',
  styleUrls: ['./header-logging-user.component.css']
})
export class HeaderLoggedInUserComponent implements OnInit {
  @Output() featureSelectedLogging = new EventEmitter<string>();
  private isAdmin: boolean;
  private prevFeature: string;
  public numOfApprove: number;

  constructor(private storeService: StoreService, private userService: UserService,
              private shareService: ShareService) {
    this.isAdmin = userService.getIsAdmin();
    this.numOfApprove = 0;
  }

  ngOnInit(): void {
    this.prevFeature = 'Products';
    this.shareService.featureSelected.subscribe(feature => {
      this.teraAll();
      if (feature === 'Owned-stores'){
        this.storeService.setOwnerStores(true);
      }else if (feature === 'Managed-stores'){
        this.storeService.setManagerStores(true);
      }
    });
  }

  onSelect(feature: string) {
    this.teraAll();
    this.storeService.setOwnerStores(feature === 'Owned-stores');
    this.storeService.setManagerStores(feature === 'Managed-stores');
    if (this.prevFeature === 'Visitor-Daily'){
      this.userService.stopDailyVisitor().subscribe();
    }
    this.prevFeature = feature;
    this.featureSelectedLogging.emit(feature);
  }

  private teraAll() {
    this.storeService.setOwnerStores(false);
    this.storeService.setManagerStores(false);
    if (this.userService.isLoggingUser()) {
      this.userService.getMyOwnerToApprove().subscribe(response => {
        if (response) {
          this.numOfApprove = response.length;
        }
      });
    }
  }

  getIsAdmin() {
     return this.isAdmin;
  }
}
