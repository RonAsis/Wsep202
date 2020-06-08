import {Component, Input, OnInit} from '@angular/core';
import {UserSystem} from '../../../../shared/userSystem.model';
import {UserService} from '../../../../services/user.service';
import {ShareService} from '../../../../services/share.service';

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.css']
})
export class UserDetailComponent implements OnInit {
  @Input() user: UserSystem;
  wantViewPurchaseHistory: boolean;
  constructor(private userService: UserService, private shareService: ShareService) { }

  ngOnInit(): void {
    this.wantViewPurchaseHistory = false;
  }

  viewPurchaseHistory() {
    this.shareService.userSelected = this.user.username;
    this.wantViewPurchaseHistory = true;
  }
}
