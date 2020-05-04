import {Component, Input, OnInit} from '@angular/core';
import {UserSystem} from '../../../../shared/userSystem.model';
import {UserService} from '../../../../services/user.service';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.css']
})
export class UserDetailComponent implements OnInit {
  @Input() user: UserSystem;
  wantViewPurchaseHistory: boolean;
  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.wantViewPurchaseHistory = false;
  }

  viewPurchaseHistory() {
    this.userService.wantViewPurchaseHistory(this.user);
    this.wantViewPurchaseHistory = true;
  }
}
