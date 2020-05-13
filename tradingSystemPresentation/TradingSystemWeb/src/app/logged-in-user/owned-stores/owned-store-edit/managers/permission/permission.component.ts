import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {Product} from '../../../../../shared/product.model';
import {UserService} from '../../../../../services/user.service';

@Component({
  selector: 'app-permission',
  templateUrl: './permission.component.html',
  styleUrls: ['./permission.component.css']
})
export class PermissionComponent implements OnInit {
  @Input()permission: string;

  @Output() permissionItemDeleted = new EventEmitter<string>();

  onPermissionItemDeleted(event) {
    this.permissionItemDeleted.emit(this.permission);
  }

  constructor() {}

  ngOnInit() {}
}
