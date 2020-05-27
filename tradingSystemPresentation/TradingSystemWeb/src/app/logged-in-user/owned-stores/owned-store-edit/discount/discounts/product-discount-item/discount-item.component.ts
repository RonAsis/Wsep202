import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Discount} from '../../../../../../shared/discount.model';
import {StoreService} from '../../../../../../services/store.service';

@Component({
  selector: 'app-discount-item',
  templateUrl: './app-discount-item.component.html',
  styleUrls: ['./app-discount-item.component.css']
})
export class DiscountItemComponent implements OnInit {

  @Output() discountWasSelected = new EventEmitter<Discount>();
  @Input() discount: Discount;
  endTime: string;

  constructor(private storeService: StoreService) {
  }

  ngOnInit(): void {
    this.endTime = new Date(this.discount.endTime).toISOString().split('T')[0];
  }

  onSelected() {
    console.log('onSelected - item discount');
    this.discountWasSelected.emit(this.discount);
    this.storeService.discountSelected.emit(this.discount);
  }
}
