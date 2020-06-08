import {Component, Input, OnInit} from '@angular/core';
import {Store} from '../../../../shared/store.model';

@Component({
  selector: 'app-policy',
  templateUrl: './policy.component.html',
  styleUrls: ['./policy.component.css']
})
export class PolicyComponent implements OnInit {

  @Input() store: Store;

  constructor() { }
  ngOnInit(): void {
  }

}
