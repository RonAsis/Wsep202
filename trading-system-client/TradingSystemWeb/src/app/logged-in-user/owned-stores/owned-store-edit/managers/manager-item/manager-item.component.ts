import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Manager} from '../../../../../shared/manager.model';
import {StoreService} from '../../../../../services/store.service';
import {Store} from '../../../../../shared/store.model';

@Component({
  selector: 'app-manager-item',
  templateUrl: './manager-item.component.html',
  styleUrls: ['./manager-item.component.css']
})
export class ManagerItemComponent implements OnInit {

  @Input()manager: Manager;
  @Input()store: Store;

  @Output() managerItemDeleted = new EventEmitter<Manager>();
  @Output() managerItemChanged = new EventEmitter<Manager>();
  permissionsCanAdd: string[];
  selectedPermission: string;
  constructor(private storeService: StoreService) { }

  ngOnInit(): void {
    this.storeService.getPermissionsCanAdd(this.manager.username, this.store.storeId)
      .subscribe(response => {
        if (response !== undefined && response !== null){
          this.permissionsCanAdd = response;
        }
      });
  }

  onManagerItemDeleted($event: MouseEvent) {
    this.managerItemDeleted.emit(this.manager);
  }

  onPermissionItemDeleted(permission: string) {
    this.storeService.removePermission(this.manager.username, this.store.storeId, permission)
      .subscribe(response => {
        if (response){
          this.manager.permissions = this.manager.permissions.filter(permissionCur => permission === permissionCur);
          this.managerItemChanged.emit(this.manager);
        }
      });
  }

  onSelectPermission(permission: string) {
      this.selectedPermission = permission;
  }

  onPermissionAdd($event: MouseEvent) {
    this.storeService.addPermission(this.store.storeId, this.manager.username, this.selectedPermission)
      .subscribe(response => {
        if (response){
          this.managerItemChanged.emit(this.manager);
        }
      });
  }
}
