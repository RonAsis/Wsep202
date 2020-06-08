import {Component, Input, OnInit} from '@angular/core';
import {Store} from '../../../../shared/store.model';
import {StoreService} from '../../../../services/store.service';
import {Manager} from '../../../../shared/manager.model';

@Component({
  selector: 'app-visible-discount-store',
  templateUrl: './managers.component.html',
  styleUrls: ['./managers.component.css']
})
export class ManagersComponent implements OnInit {

  @Input() store: Store;
  managers: Manager[];
  constructor(private storeService: StoreService) {
    this.managers = [];
  }

  ngOnInit(): void {
    this.storeService.getMySubMangers(this.store.storeId).subscribe(
      response => {
        if (response !== undefined && response !== null){
          this.managers = response;
        }
      }
    );
  }

  onManagerAdded(manager: Manager) {
    this.managers.push(manager);
  }
 onManagerItemDeleted(manager: Manager) {
    this.storeService.removeManager(manager.username, this.store.storeId)
      .subscribe(response => {
        if (response){
          this.ngOnInit();
        }
      });
  }

  onManagerItemChanged(manager: Manager) {
    this.ngOnInit();
  }
}
