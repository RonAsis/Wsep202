import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {StoreService} from '../../services/store.service';
import {WebSocketAPI} from '../../shared/apis/webSocketApi.model';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-header-logging-user',
  templateUrl: './header-logging-user.component.html',
  styleUrls: ['./header-logging-user.component.css']
})
export class HeaderLoggedInUserComponent implements OnInit {
  @Output() featureSelectedLogging = new EventEmitter<string>();
  private isAdmin: boolean;

  constructor(private storeService: StoreService, private userService: UserService) {
    this.isAdmin = userService.getIsAdmin();
  }

  ngOnInit(): void {
  }

  onSelect(feature: string) {
    this.storeService.setManagerStores(feature === 'Owned-stores');
    this.storeService.setOwnerStores(feature === 'Managed-stores');
    this.featureSelectedLogging.emit(feature);
  }

  getIsAdmin() {
    // return this.isAdmin;
    return true;
  }
}
