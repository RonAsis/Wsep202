import { Component, OnInit } from '@angular/core';
import {OwnerToApprove} from '../../shared/ownerToApprove.model';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-approve-owner',
  templateUrl: './approve-owner.component.html',
  styleUrls: ['./approve-owner.component.css']
})
export class ApproveOwnerComponent implements OnInit {

  headElements = ['Store ID', 'Store Name', 'Owner To Approve', 'Actions'];

  constructor(private userService: UserService) { }
  ownersToApproves: OwnerToApprove[];

  ngOnInit(): void {
    this.userService.getMyOwnerToApprove().subscribe(response => {
      if (response){
        this.ownersToApproves = response;
      }
    });
  }

  onApprove(el: OwnerToApprove) {
    console.log('approve');
    this.userService.approveOwner(el.storeId, el.usernameToApprove, true)
      .subscribe(response => {
        if (response){
          this.ownersToApproves = this.ownersToApproves.filter(owner => !(owner.usernameToApprove === el.usernameToApprove &&
            owner.storeId === el.storeId));
        }
      });
  }

  onNotApprove(el: OwnerToApprove) {
    this.userService.approveOwner(el.storeId, el.usernameToApprove, false)
      .subscribe(response => {
        if (response){
          this.ownersToApproves = this.ownersToApproves.filter(owner => !(owner.usernameToApprove === el.usernameToApprove &&
            owner.storeId === el.storeId));
        }
      });
  }
}
