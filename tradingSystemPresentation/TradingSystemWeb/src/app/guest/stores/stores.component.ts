import { Component, OnInit } from '@angular/core';
import {Store} from '../../shared/store.model';
import {StoreService} from '../../services/store.service';

@Component({
  selector: 'app-stores',
  templateUrl: './stores.component.html',
  styleUrls: ['./stores.component.css']
})
export class StoresComponent implements OnInit {
  selectedStore: Store;

  constructor(private storeService: StoreService) { }

  ngOnInit(): void {
    this.storeService.storeSelected
      .subscribe(
        (store: Store) => {
          this.selectedStore = store;
        }
      );
  }

}
