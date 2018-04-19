import { TestBed, async, tick, fakeAsync, inject } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Observable';

import { JhipsterTestModule } from '../../../../test.module';
import { MockActivatedRoute } from '../../../../helpers/mock-route.service';
import { TreeNavigateService } from '../../../../../../../main/webapp2/app/components/object-tree/tree-navigate/tree-navigate.service';
import { TreeNavigateComponent } from '../../../../../../../main/webapp2/app/components/object-tree/tree-navigate/tree-navigate.component';

describe('Component Tests', () => {

    describe('TreeNavigateComponent', () => {

        let comp: TreeNavigateComponent;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [JhipsterTestModule],
                declarations: [TreeNavigateComponent],
                providers: [
                    TreeNavigateService,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({'key': 'ABC123'})
                    }
                ]
            })
            .overrideTemplate(TreeNavigateComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            const fixture = TestBed.createComponent(TreeNavigateComponent);
            comp = fixture.componentInstance;
        });

        it('calls activate.get with the key from params',
            inject([TreeNavigateService],
                fakeAsync((service: TreeNavigateService) => {
                    spyOn(service, 'get').and.returnValue(Observable.of());

                    comp.ngOnInit();
                    tick();

                    expect(service.get).toHaveBeenCalledWith('ABC123');
                })
            )
        );

        xit('should set set success to OK upon successful activation',
            inject([TreeNavigateService],
                fakeAsync((service: TreeNavigateService) => {
                    spyOn(service, 'get').and.returnValue(Observable.of({}));

                    comp.ngOnInit();
                    tick();

                    expect(comp.error).toBe(null);
                    expect(comp.success).toEqual('OK');
                })
            )
        );

        xit('should set set error to ERROR upon activation failure',
            inject([ActivateService],
                fakeAsync((service: ActivateService) => {
                    spyOn(service, 'get').and.returnValue(Observable.throw('ERROR'));

                    comp.ngOnInit();
                    tick();

                    expect(comp.error).toBe('ERROR');
                    expect(comp.success).toEqual(null);
                })
            )
        );
    });
});
