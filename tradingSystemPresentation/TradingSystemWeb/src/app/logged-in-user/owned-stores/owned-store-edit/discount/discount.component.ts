import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import {FormGroup} from '@angular/forms';
import {FormlyFieldConfig, FormlyFormOptions} from '@ngx-formly/core';
import {FormlyJsonschema} from '@ngx-formly/core/json-schema';
import {Store} from '../../../../shared/store.model';
import {Manager} from '../../../../shared/manager.model';
import {StoreService} from '../../../../services/store.service';
import {Discount} from '../../../../shared/discount.model';

@Component({
  selector: 'app-discount',
  templateUrl: './discount.component.html',
  styleUrls: ['./discount.component.css']
})
export class DiscountComponent implements OnInit {

  @Input() store: Store;

  selectedDiscount: Discount;
  discountItemAChanged: Discount;

  constructor(private storeService: StoreService) { }

  ngOnInit(): void {
  }

  discountItemChanged($event: Discount) {
    this.storeService.discountAdded.emit($event);
    console.log('here !!!');
  }
}
