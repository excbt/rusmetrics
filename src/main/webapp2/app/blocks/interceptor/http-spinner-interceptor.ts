import { HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoadingStatusService } from '../../shared-blocks/exc-tools/loading-status-service';

const reqIsSpinnable = (req: HttpRequest<any>) => {
  return req.url.includes('api/');
};

export class HttpSpinnerInterceptor implements HttpInterceptor {

    constructor(private loadingStatusService: LoadingStatusService) {
    }

    private startSpin() {
        this.loadingStatusService.startRequest();
    }

    private closeSpin() {
        this.loadingStatusService.stopRequest();
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<any> {

        this.startSpin();
        return next.handle(req).pipe(
            tap( (evt) => {
                if (reqIsSpinnable(req) && evt instanceof HttpResponse) {
                    this.closeSpin();
                }
            }));

    }
}
