import { Component, OnInit } from '@angular/core';
import {Receipt} from '../../shared/receipt.model';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-history-purchase',
  templateUrl: './history-purchase.component.html',
  styleUrls: ['./history-purchase.component.css']
})
export class HistoryPurchaseComponent implements OnInit {
  receipts: Receipt[];
  receipt: string;
  searchText: string;
  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.receipts = this.userService.viewPurchaseHistory();
  }

}
