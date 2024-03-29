/* Phal AutoGenerated .ino file 
*  Created 19-06-2018
*/

#include "Lightbulb.h" 
#include "Motor.h" 
#include "PhalGroup.h"
#include "PhalList.h"
Lightbulb *lb1;
Lightbulb *lb2;
Lightbulb *lb3;
Lightbulb *lb4;
Lightbulb *lb5;
Lightbulb *lb6;
Motor *mot1;
PhalGroup *lightsleft = new PhalGroup();
PhalGroup *lightsmid = new PhalGroup();
PhalGroup *lightsright = new PhalGroup();
PhalGroup *lightsouter = new PhalGroup();
PhalGroup *lights = new PhalGroup();
void setup(){ 
lb1 = new Lightbulb(1);
lb2 = new Lightbulb(2);
lb3 = new Lightbulb(3);
lb4 = new Lightbulb(4);
lb5 = new Lightbulb(5);
lb6 = new Lightbulb(6);
mot1 = new Motor(8);
lightsleft->add(lb1);
lightsleft->add(lb2);
lightsmid->add(lb3);
lightsmid->add(lb4);
lightsright->add(lb5);
lightsright->add(lb6);
lightsouter->add(lightsleft);
lightsouter->add(lightsright);
lights->add(lightsleft);
lights->add(lightsmid);
lights->add(lightsright);
} 

void loop(){ 
lb1->on();
delay((1)*1000);
lb2->on();
delay((1)*1000);
lb3->on();
delay((1)*1000);
lb4->on();
delay((1)*1000);
lb5->on();
delay((1)*1000);
lb6->on();
delay((1)*1000);
lights->off();
delay((1)*1000);
lights->off();
lightsleft->on();
delay((1)*1000);
lightsleft->off();
lightsmid->on();
delay((1)*1000);
lightsmid->off();
lightsright->on();
delay((1)*1000);
lightsright->off();
delay((1)*1000);
lights->off();
lightsouter->on();
delay((1)*1000);
lights->off();
lightsmid->on();
delay((1)*1000);
lights->off();
mot1->on();
delay((5.2)*1000);
mot1->off();
} 

