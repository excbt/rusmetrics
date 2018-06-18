import { JhiEventManager } from 'ng-jhipster';
import { HttpInterceptor, HttpRequest, HttpErrorResponse, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/do';
import { LoadingStatusService } from '../../shared-blocks/exc-tools/loading-status-service';

export class ErrorHandlerInterceptor implements HttpInterceptor {

    constructor(private eventManager: JhiEventManager, private loadingStatusService: LoadingStatusService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).do((event: HttpEvent<any>) => {}, (err: any) => {
            if (err instanceof HttpErrorResponse) {
                this.loadingStatusService.stopRequestNoTimer();
                if (!(err.status === 401 && (err.message === '' || (err.url && err.url.indexOf('/api/account') === 0)))) {
                    if (this.eventManager !== undefined) {
                        this.eventManager.broadcast({name: 'jhipsterApp.httpError', content: err});
                    }
                }
            }
        });
    }
}
