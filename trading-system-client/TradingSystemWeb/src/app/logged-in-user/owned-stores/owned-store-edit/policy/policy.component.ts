import {Component, Input, OnInit} from '@angular/core';
import {Store} from '../../../../shared/store.model';
import {StoreService} from '../../../../services/store.service';
import {Discount} from '../../../../shared/discount.model';
import {Policy} from '../../../../shared/policy.model';

@Component({
  selector: 'app-policy',
  templateUrl: './policy.component.html',
  styleUrls: ['./policy.component.css']
})
export class PolicyComponent implements OnInit {

  @Input() store: Store;
  selectedPolicy: Policy;

  constructor(private storeService: StoreService) { }

  ngOnInit(): void {
  }

  policyItemChanged($event: Discount) {
    this.storeService.policyAdded.emit($event);
  }
}
