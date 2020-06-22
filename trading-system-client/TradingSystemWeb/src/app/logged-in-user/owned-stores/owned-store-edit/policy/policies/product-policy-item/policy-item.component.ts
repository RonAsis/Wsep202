import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Discount} from '../../../../../../shared/discount.model';
import {StoreService} from '../../../../../../services/store.service';
import {Policy} from '../../../../../../shared/policy.model';

@Component({
  selector: 'app-policy-item',
  templateUrl: './app-policy-item.component.html',
  styleUrls: ['./app-policy-item.component.css']
})
export class PolicyItemComponent implements OnInit {

  @Output() policyWasSelected = new EventEmitter<Policy>();
  @Input() policy: Policy;

  constructor(private storeService: StoreService) {
  }

  ngOnInit(): void {
  }

  onSelected() {
    console.log('onSelected - item policy');
    this.policyWasSelected.emit(this.policy);
    this.storeService.policySelected.emit(this.policy);
  }
}
