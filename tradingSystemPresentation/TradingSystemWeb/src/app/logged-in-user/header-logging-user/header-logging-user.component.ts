import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {StoreService} from '../../services/store.service';

@Component({
  selector: 'app-header-logging-user',
  templateUrl: './header-logging-user.component.html',
  styleUrls: ['./header-logging-user.component.css']
})
export class HeaderLoggingUserComponent implements OnInit {
  @Output() featureSelectedLogging = new EventEmitter<string>();

  constructor(private storeService: StoreService) { }

  ngOnInit(): void {
  }

  onSelect(feature: string) {
    this.storeService.setManagerStores(feature === 'Owned-stores');
    this.storeService.setOwnerStores(feature === 'Managed-stores');
    this.featureSelectedLogging.emit(feature);
  }
}
