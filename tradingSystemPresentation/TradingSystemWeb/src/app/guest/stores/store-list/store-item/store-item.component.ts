import {Component, Input, OnInit} from '@angular/core';
import {StoreService} from '../../../../services/store.service';
import {Store} from '../../../../shared/store.model';

@Component({
  selector: 'app-store-item',
  templateUrl: './store-item.component.html',
  styleUrls: ['./store-item.component.css']
})
export class StoreItemComponent implements OnInit {
  @Input() store: Store;

  constructor(private storeService: StoreService) { }

  ngOnInit(): void {
  }

  onSelected() {
    this.storeService.storeSelected.emit(this.store);
  }
}
