import {Component, Input, OnInit} from '@angular/core';
import {Store} from '../../../../shared/store.model';
import {StoreService} from '../../../../services/store.service';
import {Manager} from '../../../../shared/manager.model';

@Component({
  selector: 'app-managers',
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
    console.log(manager);
    this.managers.push(manager);
  }

}
