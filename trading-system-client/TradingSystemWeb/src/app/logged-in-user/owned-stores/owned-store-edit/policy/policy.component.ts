import {Component, Input, OnInit} from '@angular/core';
import {Store} from '../../../../shared/store.model';
import {StoreService} from '../../../../services/store.service';
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

  policyItemChanged($event: Policy) {
    this.storeService.policyAdded.emit($event);
  }
}
