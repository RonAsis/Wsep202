import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Store} from '../../../../../shared/store.model';
import {Options} from 'ng5-slider';
import {StoreService} from '../../../../../services/store.service';
import {UserService} from '../../../../../services/user.service';
import {Policy} from '../../../../../shared/policy.model';

@Component({
  selector: 'app-policies',
  templateUrl: './policies.component.html',
  styleUrls: ['./policies.component.css']
})
export class PoliciesComponent implements OnInit {

  @Output() policyWasSelected = new EventEmitter<Policy>();
  @Input() store: Store;
  policies: Policy [];

  constructor(private storeService: StoreService, private userService: UserService) {
    this.policies = [];
  }

  ngOnInit(): void {
    this.storeService.policyAdded.subscribe(response => {
      if (response !== undefined){
        console.log('here');
        this.policies = this.policies.filter(policy => policy.purchaseId !== response.purchaseId);
        this.policies.push(response);
      }
    });
    this.storeService.getAllPurchasePolicies(this.store.storeId)
      .subscribe(policies => {
        if (policies !== null && policies !== undefined) {
          this.policies = policies;
        }
      });
  }

  policySelected($event: Policy) {
    this.policyWasSelected.emit($event);
    console.log($event);
  }
}
