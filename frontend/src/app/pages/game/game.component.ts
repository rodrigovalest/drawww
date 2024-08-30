import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RxStompService } from '../../services/rx-stomp.service';
import { GameStateType } from '../../types/game-state.type';
import { WaitingComponent } from "../../components/waiting/waiting.component";
import { PlayingComponent } from "../../components/playing/playing.component";
import { VotingComponent } from "../../components/voting/voting.component";
import { ResultComponent } from "../../components/result/result.component";

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [WaitingComponent, PlayingComponent, VotingComponent, ResultComponent],
  templateUrl: './game.component.html'
})
export class GameComponent implements OnInit, OnDestroy {

  gameState: GameStateType = 'NOT_STARTED';

  constructor (private rxStompService: RxStompService, private router: Router) {}

  ngOnInit(): void {
    this.rxStompService.getGameState$().subscribe(state => {
      this.gameState = state;
      console.log('game state: ', state);

      if (state === 'NOT_STARTED')
        this.router.navigate([''])
    });
  }

  ngOnDestroy(): void {
    alert('sure you want to exit, some changes may be lost');
    this.rxStompService.disconnect();
  }
}
