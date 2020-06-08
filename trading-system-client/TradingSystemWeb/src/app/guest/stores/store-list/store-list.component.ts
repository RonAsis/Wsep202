import {ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Store} from '../../../shared/store.model';
import {Options} from 'ng5-slider';
import {StoreService} from '../../../services/store.service';
import {UserService} from '../../../services/user.service';

@Component({
  selector: 'app-store-list',
  templateUrl: './store-list.component.html',
  styleUrls: ['./store-list.component.css']
})
export class StoreListComponent implements OnInit {
  @Output() storeWasSelected = new EventEmitter<Store>();

  stores: Store [] ;

  value = 5;
  options: Options = {
    showTicksValues: true,
    stepsArray: [
      {value: 1, legend: 'Very poor'},
      {value: 2},
      {value: 3, legend: 'Fair'},
      {value: 4},
      {value: 5, legend: 'Average'},
      {value: 6},
      {value: 7, legend: 'Good'},
      {value: 8},
      {value: 9, legend: 'Excellent'}
    ]
  };

  constructor(private storeService: StoreService, private userService: UserService) {
    this.stores = [];
  }

  ngOnInit(): void {
    this.storeService.getStores(this.userService.getUsername(), this.userService.getUuid())
      .subscribe(stores => {
        if (stores !== null && stores !== undefined) {
          this.stores = stores;
        }
      });
  }

}
