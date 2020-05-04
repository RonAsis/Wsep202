import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {UserSystem} from '../../../../shared/userSystem.model';
import {UserService} from '../../../../services/user.service';
import {Product} from '../../../../shared/product.model';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  @Output() userWasSelected = new EventEmitter<UserSystem>();
  @Output() users: UserSystem[];
  searchText: string;

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.users = this.userService.getUsers();
  }
}
