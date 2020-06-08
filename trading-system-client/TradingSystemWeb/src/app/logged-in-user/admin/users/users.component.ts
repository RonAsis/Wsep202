import {Component, Input, OnInit} from '@angular/core';
import {UserService} from '../../../services/user.service';
import {UserSystem} from '../../../shared/userSystem.model';
import {Product} from '../../../shared/product.model';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  selectedUser: UserSystem;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.userService.userSelectedEvent
      .subscribe(
        (user: UserSystem) => {
          this.selectedUser = user;
        }
      );
  }

}
